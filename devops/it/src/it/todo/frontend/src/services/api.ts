/*
 * Copyright 2022. the original author or authors
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

import Taro from '@tarojs/taro'

export const API_URL = process.env.NODE_ENV === 'development' ? 'http://127.0.0.1:8080/api' : 'http://todo-'+process.env.NODE_ENV+'-api.dew.test/api'

console.log(Taro.getEnv())

export default {
  request(options: any, method: string = 'GET') {
    const {url} = options;
    Taro.showNavigationBarLoading();
    return Taro.request({
      ...options,
      method: method,
      url: `${API_URL}${url}`,
      header: {
        ...options.header
      },
    }).then((res) => {
      Taro.stopPullDownRefresh();
      Taro.hideNavigationBarLoading();
      if (res.statusCode === 200) {
        return res.data
      }
      throw new Error('[' + res.statusCode + ']网络请求异常')
    })
      .catch(err => {
        Taro.hideNavigationBarLoading();
        Taro.hideNavigationBarLoading();
        throw new Error(err.message);
      })
  },
  get(options: { url: string }) {
    return this.request({
      ...options
    }, 'GET')
  },
  post(options: { url: string, data?: object }) {
    return this.request({
      ...options
    }, 'POST')
  },
  put(options: { url: string, data?: object }) {
    return this.request({
      ...options
    }, 'PUT')
  },
  del(options: { url: string }) {
    return this.request({
      ...options
    }, 'DELETE')
  }
}


