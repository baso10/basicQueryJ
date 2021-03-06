/*
 * Copyright 2018 baso10.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.baso10.queryj.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QueryBuilder
{

  private String select = "*";
  private boolean distinct;
  private QJTable from;
  private final List<QJJoin> leftJoin = new ArrayList<>();
  private final List<QJJoin> innerJoin = new ArrayList<>();
  private final List<QJWhere> where = new ArrayList<>();
  private final List<QJParam> params = new ArrayList<>();
  private String groupBy;
  private Integer limit;
  private Integer offset;
  private String orderBy;
  private int paramIndex = 1;
  private String paramKey = "p";

  public QueryBuilder()
  {
  }

  public QueryBuilder(String paramKey)
  {
    this.paramKey = paramKey;
  }

  public QueryBuilder from(String tableName)
  {
    this.from = new QJTable(tableName, "t");
    return this;
  }

  public QueryBuilder from(String tableName, String alias)
  {
    this.from = new QJTable(tableName, alias);
    return this;
  }

  public QueryBuilder select(String select)
  {
    this.select = select;
    return this;
  }

  public QueryBuilder andWhere(String where, QJParam... params)
  {
    this.where.add(new QJWhere("AND", where));
    if(params != null)
    {
      this.params.addAll(Arrays.asList(params));
    }
    return this;
  }

  public QueryBuilder andWhere(QueryBuilder where, QJParam... params)
  {
    this.where.add(new QJWhere("AND", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public QueryBuilder orWhere(String where, QJParam... params)
  {
    this.where.add(new QJWhere("OR", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public QueryBuilder orWhere(QueryBuilder where, QJParam... params)
  {
    this.where.add(new QJWhere("OR", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public QueryBuilder orderBy(String orderBy)
  {
    this.orderBy = orderBy;
    return this;
  }

  public String getSql()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT ");
    if(distinct)
    {
      sb.append("DISTINCT ");
    }
    sb.append(select);
    sb.append(" FROM ").append(from.getName());
    if(from.getAlias() != null)
    {
      sb.append(" ").append(from.getAlias());
    }

    if(!this.innerJoin.isEmpty())
    {
      for(QJJoin join : innerJoin)
      {
        QJTable table = join.getTable();
        QJWhere condition = join.getCondition();
        sb.append(" INNER JOIN ").append(table.getName());
        if(table.getAlias() != null)
        {
          sb.append(" ").append(table.getAlias());
        }
        sb.append(" ON ");
        sb.append(condition.getCondition());
      }
    }

    if(!this.leftJoin.isEmpty())
    {
      for(QJJoin join : leftJoin)
      {
        QJTable table = join.getTable();
        QJWhere condition = join.getCondition();
        sb.append(" LEFT JOIN ").append(table.getName());
        if(table.getAlias() != null)
        {
          sb.append(" ").append(table.getAlias());
        }
        sb.append(" ON ");
        sb.append(condition.getCondition());
      }
    }

    if(!this.where.isEmpty())
    {
      sb.append(" WHERE ");
      sb.append(getWhereSql());
    }

    if(this.groupBy != null)
    {
      sb.append(" GROUP BY ").append(this.groupBy);
    }

    if(this.orderBy != null)
    {
      sb.append(" ORDER BY ").append(this.orderBy);
    }

    if(this.limit != null)
    {
      sb.append(" LIMIT ").append(this.limit);

    }
    if(this.offset != null && this.offset > 0)
    {
      if(this.limit == null)
      {
        sb.append(" LIMIT ").append(Integer.MAX_VALUE);
      }
      sb.append(" OFFSET ").append(this.offset);
    }

    return sb.toString();
  }

  public Map<String, Object> getParams()
  {
    return this.params.stream().collect(Collectors.toMap(QJParam::getName, QJParam::getValue, (key, value) -> key));
  }

  protected List<QJParam> getParamsList()
  {
    return this.params;
  }

  protected String getWhereSql()
  {
    StringBuilder sb = new StringBuilder();
    if(!this.where.isEmpty())
    {
      boolean first = true;
      for(QJWhere whereObj : where)
      {
        QueryBuilder subQuery = whereObj.getQuery();
        if(subQuery != null)
        {
          if(!first)
          {
            sb.append(" ").append(whereObj.getOperator()).append(" ");
          }
          sb.append("(").append(subQuery.getWhereSql()).append(")");
          this.params.addAll(subQuery.getParamsList());
        }
        else
        {
          if(!first)
          {
            sb.append(" ").append(whereObj.getOperator()).append(" ");
          }
          sb.append(whereObj.getCondition());
        }
        first = false;
      }
    }

    return sb.toString();
  }

  public QueryBuilder andCompare(String field, Object value)
  {
    return andCompare(field, value, String.class);
  }
  
  public QueryBuilder andCompare(String fieldTableAlias, String field, Object value, Class classType)
  {
    return andCompare((fieldTableAlias == null ? "" : (fieldTableAlias + ".")) + field, value, classType);
  }

  public QueryBuilder andCompare(String field, Object value, Class classType)
  {
    if(value == null || "".equals(value) || (value instanceof String && "".equals(((String) value).trim())))
    {
      return this;
    }

    String param = paramKey + paramIndex++;
    String operation = null;
    if(value instanceof String)
    {
      String stringValue = (String) value;

      String regex = "^(<>|>=|>|<=|<|=)";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(stringValue);

      if(matcher.find())
      {
        int end = matcher.end();
        operation = matcher.group();
        value = stringValue.substring(end);
      }
    }

    if(Number.class.isAssignableFrom(classType) || value instanceof Number)
    {
      try
      {

        if(Integer.class.isAssignableFrom(classType) || value instanceof Integer)
        {
          value = Integer.parseInt("" + value);
        }
        else if(Long.class.isAssignableFrom(classType) || value instanceof Long)
        {
          value = Long.parseLong("" + value);
        }
        else if(Double.class.isAssignableFrom(classType) || value instanceof Double)
        {
          value = Double.parseDouble("" + value);
        }
        String not = "<>".equals(operation) ? "NOT " : "";
        if(operation == null || "<>".equals(operation))
        {
          operation = "=";
        }
        this.where.add(new QJWhere("AND", not + field + " " + operation + " :" + param));
        this.params.add(new QJParam(param, value));

      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      String stringValue = "" + value;
      //contains by default
      if(operation == null)
      {
        if(stringValue.contains("*"))
        {
          //manual like %
          stringValue = stringValue.replaceAll("\\*", "%");
        }
        else
        {
          stringValue = "%" + stringValue + "%";
        }
        this.where.add(new QJWhere("AND", field + " LIKE :" + param));
        this.params.add(new QJParam(param, stringValue));
      }
      else if("=".equals(operation))
      {
        this.where.add(new QJWhere("AND", field + " = :" + param));
        this.params.add(new QJParam(param, stringValue));
      }
      else if("<>".equals(operation))
      {
        this.where.add(new QJWhere("AND", "NOT " + field + " = :" + param));
        this.params.add(new QJParam(param, stringValue));
      }
      else if(">".equals(operation))
      {
        stringValue = "%" + stringValue;
        this.where.add(new QJWhere("AND", field + " LIKE :" + param));
        this.params.add(new QJParam(param, stringValue));
      }
      else if("<".equals(operation))
      {
        stringValue = stringValue + "%";
        this.where.add(new QJWhere("AND", field + " LIKE :" + param));
        this.params.add(new QJParam(param, stringValue));
      }
    }
    return this;
  }

  public QueryBuilder limit(Integer limit)
  {
    this.limit = limit;
    return this;
  }

  public QueryBuilder offset(Integer offset)
  {
    this.offset = offset;
    return this;
  }

  public QueryBuilder groupBy(String groupBy)
  {
    this.groupBy = groupBy;
    return this;
  }

  public QueryBuilder leftJoin(String table, String alias, String on, QJParam... params)
  {
    this.leftJoin.add(new QJJoin(new QJTable(table, alias), new QJWhere(null, on)));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public QueryBuilder innerJoin(String table, String alias, String on, QJParam... params)
  {
    this.innerJoin.add(new QJJoin(new QJTable(table, alias), new QJWhere(null, on)));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public QueryBuilder addParam(String name, Object value)
  {
    this.params.add(QJParam.p(name, value));
    return this;
  }

  public QueryBuilder distinct(boolean distinct)
  {
    this.distinct = distinct;
    return this;
  }
}
