/*
 * Copyright 2018 ethereumKeyJ.
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
package org.basic.queryj.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Query
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

  public Query()
  {
  }

  public Query(String paramKey)
  {
    this.paramKey = paramKey;
  }

  public Query from(String tableName)
  {
    this.from = new QJTable(tableName, "t");
    return this;
  }

  public Query from(String tableName, String alias)
  {
    this.from = new QJTable(tableName, alias);
    return this;
  }

  public Query select(String select)
  {
    this.select = select;
    return this;
  }

  public Query andWhere(String where, QJParam... params)
  {
    this.where.add(new QJWhere("AND", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Query andWhere(Query where, QJParam... params)
  {
    this.where.add(new QJWhere("AND", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Query orWhere(String where, QJParam... params)
  {
    this.where.add(new QJWhere("OR", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Query orWhere(Query where, QJParam... params)
  {
    this.where.add(new QJWhere("OR", where));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Query orderBy(String orderBy)
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
    if(this.offset != null)
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
        Query subQuery = whereObj.getQuery();
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

  public Query andCompare(String field, Object value)
  {
    if(value == null || "".equals(value) || (value instanceof String && "".equals(((String) value).trim())))
    {
      return this;
    }

    String param = ":" + paramKey + paramIndex++;
    if(value instanceof String)
    {
      String stringValue = (String) value;

      String regex = "^(<>|>=|>|<=|<|=)";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(stringValue);

      String operation = null;

      if(matcher.find())
      {
        int end = matcher.end();
        operation = matcher.group();
        stringValue = stringValue.substring(end);
      }

      try
      {
        int intValue = Integer.parseInt(stringValue);
        String not = "<>".equals(operation) ? "NOT " : "";
        if(operation == null || "<>".equals(operation))
        {
          operation = "=";
        }
        this.where.add(new QJWhere("AND", not + field + " " + operation + " " + param));
        this.params.add(new QJParam(param, intValue));
      }
      catch(NumberFormatException e)
      {
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
          this.where.add(new QJWhere("AND", field + " LIKE " + param));
          this.params.add(new QJParam(param, stringValue));
        }
        else if("=".equals(operation))
        {
          this.where.add(new QJWhere("AND", field + " = " + param));
          this.params.add(new QJParam(param, stringValue));
        }
        else if("<>".equals(operation))
        {
          this.where.add(new QJWhere("AND", "NOT " + field + " = " + param));
          this.params.add(new QJParam(param, stringValue));
        }
        else if(">".equals(operation))
        {
          stringValue = "%" + stringValue;
          this.where.add(new QJWhere("AND", field + " LIKE " + param));
          this.params.add(new QJParam(param, stringValue));
        }
        else if("<".equals(operation))
        {
          stringValue = stringValue + "%";
          this.where.add(new QJWhere("AND", field + " LIKE " + param));
          this.params.add(new QJParam(param, stringValue));
        }
      }

    }
    else if(value instanceof Number)
    {
      this.where.add(new QJWhere("AND", field + " = " + param));
      this.params.add(new QJParam(param, value));
    }

    return this;
  }

  public Query limit(Integer limit)
  {
    this.limit = limit;
    return this;
  }

  public Query offset(Integer offset)
  {
    this.offset = offset;
    return this;
  }

  public Query groupBy(String groupBy)
  {
    this.groupBy = groupBy;
    return this;
  }

  public Query leftJoin(String table, String alias, String on, QJParam... params)
  {
    this.leftJoin.add(new QJJoin(new QJTable(table, alias), new QJWhere(null, on)));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Query innerJoin(String table, String alias, String on, QJParam... params)
  {
    this.innerJoin.add(new QJJoin(new QJTable(table, alias), new QJWhere(null, on)));
    this.params.addAll(Arrays.asList(params));
    return this;
  }

  public Query addParam(String name, Object value)
  {
    this.params.add(QJParam.p(name, value));
    return this;
  }
  
  public Query distinct(boolean distinct)
  {
    this.distinct = distinct;
    return this;
  }
}
