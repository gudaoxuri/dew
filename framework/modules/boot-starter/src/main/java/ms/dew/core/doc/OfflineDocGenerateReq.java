/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.core.doc;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfflineDocGenerateReq {

    @NotNull
    private String docName;
    private String docDesc;
    @NotEmpty
    private Map<String, String> visitUrls = new HashMap<>();

    private List<String> swaggerJsonUrls;

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    public Map<String, String> getVisitUrls() {
        return visitUrls;
    }

    public void setVisitUrls(Map<String, String> visitUrls) {
        this.visitUrls = visitUrls;
    }

    public List<String> getSwaggerJsonUrls() {
        return swaggerJsonUrls;
    }

    public void setSwaggerJsonUrls(List<String> swaggerJsonUrls) {
        this.swaggerJsonUrls = swaggerJsonUrls;
    }
}
