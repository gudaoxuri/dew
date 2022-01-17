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

package group.idealworld.dew.core.dbutils.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页辅助类
 */
@Data
public class Page<E> {

    //start with 1
    private long pageNumber;
    private long pageSize;
    private long pageTotal;
    private long recordTotal;
    private List<E> objects;

}
