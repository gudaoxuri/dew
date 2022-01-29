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

import Taro, {Component, Config} from '@tarojs/taro'
import {View} from '@tarojs/components'
import {AtInput, AtList, AtListItem, AtSwipeAction} from 'taro-ui'
import api from '../../services/api'
import {DewHeader} from '../../components/dew-header/dew-header'
import './todo.scss'

const OPTIONS = [
  {
    text: '删除',
    style: {
      color: '#fff',
      backgroundColor: '#ff0000'
    }
  }
]

export default class Todo extends Component {

  config: Config = {
    navigationBarTitleText: 'Todo'
  }

  state = {
    newItem: '',
    currPage: 1,
    hasNext: true,
    items: []
  }

  componentDidMount() {
    this.fetchItems();
  }

 /* onPullDownRefresh() {
    this.fetchItems();
  }*/

  onReachBottom() {
    this.fetchItems(true);
  }

  private async fetchItems(next: boolean = false) {
    if (!this.state.hasNext) {
      Taro.atMessage({
        'message': '没有更多任务了'
      })
      return
    }
    let items = await api.get({
      url: '/?pageNumber=' + (next ? this.state.currPage + 1 : this.state.currPage),
    })
    this.setState({
      currPage: items.pageNumber,
      hasNext: items.pageNumber < items.pageTotal,
      items: this.state.items.concat(items.objects)
    })
  }


  async addItem(value) {
    let items = this.state.items
    let item = await api.post({
      url: '/',
      data: value
    })
    items.push({
      content: item.content,
      id: item.id,
      order: item.order
    })
    this.setState({
      newItem: '',
      items
    })
  }

  async deleteItem(id) {
    await api.del({
      url: '/' + id,
      dataType: 'text'
    })
    let items = this.state.items.filter(item => item.id !== id);
    this.setState({
      items
    })
  }

  render() {

    return (
      <View className='page'>
        <DewHeader title='我的任务' desc=''></DewHeader>
        <View className='dew-body'>
          <AtInput
            name='new-item'
            type='text'
            placeholder='添加任务'
            value={this.state.newItem}
            onConfirm={this.addItem.bind(this)}
          />
          <AtList>
            {this.state.items.map((item) => (
              <AtSwipeAction
                key={item.id}
                isOpened={false}
                onOpened={this.deleteItem.bind(this, item.id)}
                options={OPTIONS}
              >
                <AtListItem title={item.content}/>
              </AtSwipeAction>
            ))}
          </AtList>
        </View>
      </View>
    )
  }
}
