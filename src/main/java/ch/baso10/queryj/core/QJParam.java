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
public class QJParam
{

  private final String name;
  private final Object value;

  public QJParam(String name, Object value)
  {
    this.name = name;
    this.value = value;
  }

  public static QJParam p(String name, Object value)
  {
    return new QJParam(name, value);
  }

  public String getName()
  {
    return name;
  }

  public Object getValue()
  {
    return value;
  }

}
