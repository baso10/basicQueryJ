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

/**
 *
 * @author baso10
 */
public class QJTable
{

  private final String name;
  private final String alias;

  protected QJTable(String name, String alias)
  {
    this.name = name;
    this.alias = alias;
  }

  public String getName()
  {
    return name;
  }

  public String getAlias()
  {
    return alias;
  }

}
