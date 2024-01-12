import Taro, {Component} from '@tarojs/taro'
import {View} from '@tarojs/components'
import './dew-header.scss'

export class DewHeader extends Component {

  render() {
    return (
      <View className='dew-header'>
        <View className='header__title'>{this.props.title}</View>
        <View className='header__desc'>{this.props.desc}</View>
      </View>
    )
  }
}

