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

package group.idealworld.dew.core.doc;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Offline doc generate req.
 *
 * @author gudaoxuri
 */
public class OfflineDocGenerateReq {

    @NotNull
    private String docName;
    private String docDesc;
    @NotEmpty
    private Map<String, String> visitUrls = new HashMap<>();

    private List<String> swaggerJsonUrls;

    /**
     * Gets doc name.
     *
     * @return the doc name
     */
    public String getDocName() {
        return docName;
    }

    /**
     * Sets doc name.
     *
     * @param docName the doc name
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * Gets doc desc.
     *
     * @return the doc desc
     */
    public String getDocDesc() {
        return docDesc;
    }

    /**
     * Sets doc desc.
     *
     * @param docDesc the doc desc
     */
    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    /**
     * Gets visit urls.
     *
     * @return the visit urls
     */
    public Map<String, String> getVisitUrls() {
        return visitUrls;
    }

    /**
     * Sets visit urls.
     *
     * @param visitUrls the visit urls
     */
    public void setVisitUrls(Map<String, String> visitUrls) {
        this.visitUrls = visitUrls;
    }

    /**
     * Gets swagger json urls.
     *
     * @return the swagger json urls
     */
    public List<String> getSwaggerJsonUrls() {
        return swaggerJsonUrls;
    }

    /**
     * Sets swagger json urls.
     *
     * @param swaggerJsonUrls the swagger json urls
     */
    public void setSwaggerJsonUrls(List<String> swaggerJsonUrls) {
        this.swaggerJsonUrls = swaggerJsonUrls;
    }
}
