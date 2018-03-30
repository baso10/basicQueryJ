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
package org.basic.queryjJcore;

import org.basic.queryj.core.Query;
import org.basic.queryj.core.QJParam;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestQuery
{

  @Test
  public void testQuery()
  {
    assertEquals("SELECT * FROM MyTable t", new Query().from("MyTable").getSql());
    assertEquals("SELECT * FROM MyTable a", new Query().from("MyTable", "a").getSql());
    assertEquals("SELECT * FROM MyTable t", new Query().select("*").from("MyTable").getSql());
    assertEquals("SELECT t.id, t.name FROM MyTable t", new Query().select("t.id, t.name").from("MyTable").getSql());
  }

  @Test
  public void testQuery2()
  {
    Query query = new Query().select("t.id, t.name").from("MyTable").andWhere("t.id = :id", new QJParam(":id", 1));
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
  }

  @Test
  public void testQuery3()
  {
    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andWhere("t.id = :id", new QJParam(":id", 1))
            .andWhere("t.name = :name", new QJParam(":name", "My name"))
            .andWhere("t.col3 = :col3", new QJParam(":col3", true))
            .andWhere("col4 = :col4 AND col5 = :col5", new QJParam(":col4", "col4"), new QJParam(":col5", "col5"));
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id AND t.name = :name AND t.col3 = :col3 AND col4 = :col4 AND col5 = :col5", query.getSql());
    assertEquals(5, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
    assertEquals("My name", query.getParams().get(":name"));
    assertEquals(true, query.getParams().get(":col3"));
    assertEquals("col4", query.getParams().get(":col4"));
    assertEquals("col5", query.getParams().get(":col5"));
  }

  @Test
  public void testQuery4()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andWhere("t.id = :id", new QJParam(":id", 1))
            .orWhere("t.name = :name", new QJParam(":name", "My name"))
            .andWhere("t.col3 = :col3", new QJParam(":col3", true))
            .andWhere("col4 = :col4 AND col5 = :col5", new QJParam(":col4", "col4"), new QJParam(":col5", "col5"));
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id OR t.name = :name AND t.col3 = :col3 AND col4 = :col4 AND col5 = :col5", query.getSql());
    assertEquals(5, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
    assertEquals("My name", query.getParams().get(":name"));

  }

  @Test
  public void testQuery5()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andWhere("t.id = :id", new QJParam(":id", 1))
            .andCompare("t.name", "My name");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id AND t.name LIKE :p1", query.getSql());
    assertEquals(2, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
    assertEquals("%My name%", query.getParams().get(":p1"));
  }

  @Test
  public void testQuery6()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andWhere("t.id = :id", new QJParam(":id", 1))
            .andCompare("t.name", "My name")
            .andCompare("t.col3", "col3");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id AND t.name LIKE :p1 AND t.col3 LIKE :p2", query.getSql());
    assertEquals(3, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
    assertEquals("%My name%", query.getParams().get(":p1"));
    assertEquals("%col3%", query.getParams().get(":p2"));
  }

  @Test
  public void testQuery7()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andWhere("t.id = :id", new QJParam(":id", 1))
            .andCompare("t.name", "My name")
            .andWhere("t.col4 LIKE :col4", new QJParam(":col4", 4))
            .andCompare("t.col3", "col3");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id AND t.name LIKE :p1 AND t.col4 LIKE :col4 AND t.col3 LIKE :p2", query.getSql());
    assertEquals(4, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
    assertEquals("%My name%", query.getParams().get(":p1"));
    assertEquals(4, query.getParams().get(":col4"));
    assertEquals("%col3%", query.getParams().get(":p2"));
  }

  @Test
  public void testQuery8()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andWhere("t.id = :id", new QJParam(":id", 1))
            .andCompare("t.name", "*My name")
            .andCompare("t.col3", "")
            .andCompare("t.col3", null)
            .andCompare("t.col3", "  ");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.id = :id AND t.name LIKE :p1", query.getSql());
    assertEquals(2, query.getParams().size());
    assertEquals(1, query.getParams().get(":id"));
    assertEquals("%My name", query.getParams().get(":p1"));
  }

  @Test
  public void testQuery9()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col3", 0);
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col3 = :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(0, query.getParams().get(":p1"));
  }

  @Test
  public void testQuery10()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col3", "0");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col3 = :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(0, query.getParams().get(":p1"));
  }

  @Test
  public void testQuery11()
  {

    Query and1Query = new Query("r").andCompare("t.col1", "1").andCompare("t.col2", "My");
    Query and2Query = new Query("s").andCompare("t.col3", "3").andCompare("t.col4", "My4*");

    Query query = new Query().from("MyTable", "t")
            .andWhere(and1Query).orWhere(and2Query);

    assertEquals("SELECT * FROM MyTable t WHERE (t.col1 = :r1 AND t.col2 LIKE :r2) OR (t.col3 = :s1 AND t.col4 LIKE :s2)", query.getSql());
    assertEquals(4, query.getParams().size());
    assertEquals(1, query.getParams().get(":r1"));
    assertEquals("%My%", query.getParams().get(":r2"));
    assertEquals(3, query.getParams().get(":s1"));
    assertEquals("My4%", query.getParams().get(":s2"));
  }

  @Test
  public void testQuery12()
  {

    Query and1Query = new Query("r").andCompare("t.col1", "1").andCompare("t.col2", "My");
    Query and2Query = new Query("s")
            .andCompare("t.col3", "3")
            .andCompare("t.col4", "My4*")
            .andWhere("EXISTS (SELECT 1 FROM NewTable nt WHERE nt.refId = t.id AND nt.col = :ntCol1 AND nt.col2 = :ntCol2)",
                    QJParam.p(":ntCol1", 123), QJParam.p(":ntCol2", 23));

    Query query = new Query().distinct(true).from("MyTable", "t")
            .andWhere(and1Query).orWhere(and2Query);

    assertEquals("SELECT DISTINCT * FROM MyTable t WHERE (t.col1 = :r1 AND t.col2 LIKE :r2) OR (t.col3 = :s1 AND t.col4 LIKE :s2 AND EXISTS (SELECT 1 FROM NewTable nt WHERE nt.refId = t.id AND nt.col = :ntCol1 AND nt.col2 = :ntCol2))", query.getSql());
    assertEquals(6, query.getParams().size());
    assertEquals(1, query.getParams().get(":r1"));
    assertEquals("%My%", query.getParams().get(":r2"));
    assertEquals(3, query.getParams().get(":s1"));
    assertEquals("My4%", query.getParams().get(":s2"));
    assertEquals(123, query.getParams().get(":ntCol1"));
    assertEquals(23, query.getParams().get(":ntCol2"));
  }

  @Test
  public void testDuplicateParams()
  {
    Query query = new Query().from("MyTable", "t").andWhere("a = :a AND b = :a", QJParam.p(":a", 1));
    assertEquals("SELECT * FROM MyTable t WHERE a = :a AND b = :a", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(1, query.getParams().get(":a"));
  }
  
  @Test
  public void testGroupBy()
  {
    Query query = new Query().select("MAX(id) AS id").from("MyTable", "t").groupBy("t.name");
    assertEquals("SELECT MAX(id) AS id FROM MyTable t GROUP BY t.name", query.getSql());
    assertEquals(0, query.getParams().size());
  }
  
  @Test
  public void testGroupBy2()
  {
    Query query = new Query().select("MAX(id) AS id").from("MyTable", "t").groupBy("t.name HAVING COUNT(id) > 1");
    assertEquals("SELECT MAX(id) AS id FROM MyTable t GROUP BY t.name HAVING COUNT(id) > 1", query.getSql());
    assertEquals(0, query.getParams().size());
  }

  @Test
  public void testOrderBy()
  {
    Query query = new Query().from("MyTable", "t").orderBy("t.id");
    assertEquals("SELECT * FROM MyTable t ORDER BY t.id", query.getSql());
    assertEquals(0, query.getParams().size());
  }

  @Test
  public void testLimitOffset1()
  {
    Query query = new Query().from("MyTable", "t").orWhere("a = 1").orderBy("id").limit(10).offset(10);
    assertEquals("SELECT * FROM MyTable t WHERE a = 1 ORDER BY id LIMIT 10 OFFSET 10", query.getSql());
  }

  @Test
  public void testLimitOffset2()
  {
    Query query = new Query().from("MyTable", "t").limit(100);
    assertEquals("SELECT * FROM MyTable t LIMIT 100", query.getSql());
  }

  @Test
  public void testLimitOffset3()
  {
    Query query = new Query().from("MyTable", "t").offset(100);
    assertEquals("SELECT * FROM MyTable t LIMIT 2147483647 OFFSET 100", query.getSql());
  }

  @Test
  public void testLimitOffset4()
  {
    Query query = new Query().from("MyTable", "t").offset(100).limit(10);
    assertEquals("SELECT * FROM MyTable t LIMIT 10 OFFSET 100", query.getSql());
  }

  @Test
  public void testJoin1()
  {
    Query query = new Query().from("MyTable", "t").leftJoin("JoinTable", "jt", "jt.refId = t.id");
    assertEquals("SELECT * FROM MyTable t LEFT JOIN JoinTable jt ON jt.refId = t.id", query.getSql());
  }

  @Test
  public void testJoin2()
  {
    Query query = new Query().from("MyTable", "t")
            .leftJoin("JoinTable", "jt", "jt.refId = t.id")
            .leftJoin("JoinTable2", "jt2", "jt2.refId = jt.id");
    assertEquals("SELECT * FROM MyTable t LEFT JOIN JoinTable jt ON jt.refId = t.id LEFT JOIN JoinTable2 jt2 ON jt2.refId = jt.id", query.getSql());
  }

  @Test
  public void testJoin3()
  {
    Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id");
    assertEquals("SELECT * FROM MyTable t INNER JOIN JoinTable jt ON jt.refId = t.id", query.getSql());
  }

  @Test
  public void testJoin4()
  {
    Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id")
            .innerJoin("JoinTable2", "jt2", "jt2.refId = jt.id");
    assertEquals("SELECT * FROM MyTable t INNER JOIN JoinTable jt ON jt.refId = t.id INNER JOIN JoinTable2 jt2 ON jt2.refId = jt.id", query.getSql());
  }

  @Test
  public void testJoin5()
  {
    Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id")
            .leftJoin("JoinTable2", "jt2", "jt2.refId = jt.id")
            .limit(10).offset(100);
    assertEquals("SELECT * FROM MyTable t INNER JOIN JoinTable jt ON jt.refId = t.id LEFT JOIN JoinTable2 jt2 ON jt2.refId = jt.id LIMIT 10 OFFSET 100", query.getSql());
  }

  @Test
  public void testJoin6()
  {
    Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id AND jt.status = :status").addParam(":status", 1)
            .leftJoin("JoinTable2", "jt2", "jt2.refId = jt.id")
            .limit(10).offset(100);
    assertEquals("SELECT * FROM MyTable t INNER JOIN JoinTable jt ON jt.refId = t.id AND jt.status = :status LEFT JOIN JoinTable2 jt2 ON jt2.refId = jt.id LIMIT 10 OFFSET 100", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(1, query.getParams().get(":status"));
  }
  
  @Test
  public void testJoin7()
  {
    Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id AND jt.status = :status", QJParam.p(":status", 1))
            .leftJoin("JoinTable2", "jt2", "jt2.refId = jt.id")
            .limit(10).offset(100)
            .andWhere("t.val = 2");
    assertEquals("SELECT * FROM MyTable t INNER JOIN JoinTable jt ON jt.refId = t.id AND jt.status = :status LEFT JOIN JoinTable2 jt2 ON jt2.refId = jt.id WHERE t.val = 2 LIMIT 10 OFFSET 100", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(1, query.getParams().get(":status"));
  }
  
  @Test
  public void testJoin8()
  {
    Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id AND jt.status = :status", QJParam.p(":status", 1))
            .leftJoin("JoinTable2", "jt2", "jt2.refId = jt.id")
            .limit(10).offset(100)
            .andWhere("t.val = :val").addParam(":val", 11);
    assertEquals("SELECT * FROM MyTable t INNER JOIN JoinTable jt ON jt.refId = t.id AND jt.status = :status LEFT JOIN JoinTable2 jt2 ON jt2.refId = jt.id WHERE t.val = :val LIMIT 10 OFFSET 100", query.getSql());
    assertEquals(2, query.getParams().size());
    assertEquals(1, query.getParams().get(":status"));
    assertEquals(11, query.getParams().get(":val"));
  }
  
  @Test
  public void testCompare1()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.name", ">My name");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.name LIKE :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals("%My name", query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare2()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.name", "<My name");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.name LIKE :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals("My name%", query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare3()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.name", "<>My name");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE NOT t.name = :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals("My name", query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare4()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.name", "=My name");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.name = :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals("My name", query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare5()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col", "=10");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col = :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(10, query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare6()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col", ">10");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col > :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(10, query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare7()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col", ">=10");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col >= :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(10, query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare8()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col", "<10");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col < :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(10, query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare9()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col", "<=10");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE t.col <= :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(10, query.getParams().get(":p1"));
  }
  
  @Test
  public void testCompare10()
  {

    Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.col", "<>10");
    assertEquals("SELECT t.id, t.name FROM MyTable t WHERE NOT t.col = :p1", query.getSql());
    assertEquals(1, query.getParams().size());
    assertEquals(10, query.getParams().get(":p1"));
  }
}
