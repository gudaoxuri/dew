/*
 * Copyright 2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.dbutils;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class User {

    private long id;
    private String name;
    private String password;
    private int age;
    private float height1;
    private double height2;
    private Date createTime;
    private BigDecimal asset;
    private String txt;
    private boolean enable;

}
