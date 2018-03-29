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
package org.basic.queryj.core;

/**
 *
 * @author baso10
 */
public class QJJoin
{

  private final QJTable table;
  private final QJWhere condition;

  protected QJJoin(QJTable table, QJWhere condition)
  {
    this.table = table;
    this.condition = condition;
  }

  public QJTable getTable()
  {
    return table;
  }

  public QJWhere getCondition()
  {
    return condition;
  }

}
