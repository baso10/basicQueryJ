[![Build Status](https://travis-ci.org/baso10/basicQueryJ.svg?branch=master)](https://travis-ci.org/baso10/basicQueryJ)
[![Coverage Status](https://coveralls.io/repos/github/baso10/basicQueryJ/badge.svg?branch=master)](https://coveralls.io/github/baso10/basicQueryJ?branch=master)

# SQL QueryJ

Java library to build SQL queryies.

# Usage
```
Query query = new Query().select("t.id, t.name").from("MyTable").andWhere("t.id = :id", new QJParam(":id", 1));
query.getSql();
query.getParams();
SELECT t.id, t.name FROM MyTable t WHERE t.id = :id
```
```
Query query = new Query().select("t.id, t.name").from("MyTable")
            .andCompare("t.name", "My name")
            .andCompare("t.col3", "col3");
query.getSql();
query.getParams();
SELECT t.id, t.name FROM MyTable t WHERE t.name LIKE :p1 AND t.col3 LIKE :p2
```
```
Query and1Query = new Query("r").andCompare("t.col1", "1").andCompare("t.col2", "My");
Query and2Query = new Query("s")
        .andCompare("t.col3", "3")
        .andCompare("t.col4", "My4*")
        .andWhere("EXISTS (SELECT 1 FROM NewTable nt WHERE nt.refId = t.id AND nt.col = :ntCol1 AND nt.col2 = :ntCol2)",
                QJParam.p("ntCol1", 123), QJParam.p("ntCol2", 23));

Query query = new Query().from("MyTable", "t")
        .andWhere(and1Query).orWhere(and2Query);
query.getSql();    
```
```
Query query = new Query().from("MyTable", "t")
            .innerJoin("JoinTable", "jt", "jt.refId = t.id AND jt.status = :status", QJParam.p("status", 1))
            .leftJoin("JoinTable2", "jt2", "jt2.refId = jt.id")
            .limit(10).offset(100);
query.getSql();  
```
License
-------
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
