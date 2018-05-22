package io.shardingjdbc.core.constant;

import com.google.common.base.Joiner;
import io.shardingjdbc.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;


public final class ShardingProperties {

    private final Properties props;

    public ShardingProperties(final Properties props) {
        this.props = props;
        validate();
    }

    private void validate() {
        Set<String> propertyNames = props.stringPropertyNames();
        Collection<String> errorMessages = new ArrayList<>(propertyNames.size());
        for (String each : propertyNames) {
            ShardingPropertiesConstant shardingPropertiesConstant = ShardingPropertiesConstant.findByKey(each);
            if (null == shardingPropertiesConstant) {
                continue;
            }
            Class<?> type = shardingPropertiesConstant.getType();
            String value = props.getProperty(each);
            if (type == boolean.class && !StringUtil.isBooleanValue(value)) {
                errorMessages.add(getErrorMessage(shardingPropertiesConstant, value));
                continue;
            }
            if (type == int.class && !StringUtil.isIntValue(value)) {
                errorMessages.add(getErrorMessage(shardingPropertiesConstant, value));
                continue;
            }
            if (type == long.class && !StringUtil.isLongValue(value)) {
                errorMessages.add(getErrorMessage(shardingPropertiesConstant, value));
            }
        }
        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(Joiner.on(" ").join(errorMessages));
        }
    }

    private String getErrorMessage(final ShardingPropertiesConstant shardingPropertiesConstant, final String invalidValue) {
        return String.format("Value '%s' of '%s' cannot convert to type '%s'.", invalidValue, shardingPropertiesConstant.getKey(), shardingPropertiesConstant.getType().getName());
    }

    /**
     * Get property value.
     *
     * @param shardingPropertiesConstant sharding properties constant
     * @param <T>                        class type of return value
     * @return property value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(final ShardingPropertiesConstant shardingPropertiesConstant) {
        if (shardingPropertiesConstant.equals(ShardingPropertiesConstant.SQL_SHOW)) {
            return (T) props.get(ShardingPropertiesConstant.SQL_SHOW.getKey());
        }
        String result = props.getProperty(shardingPropertiesConstant.getKey(), shardingPropertiesConstant.getDefaultValue());
        if (boolean.class == shardingPropertiesConstant.getType()) {
            return (T) Boolean.valueOf(result);
        }
        if (int.class == shardingPropertiesConstant.getType()) {
            return (T) Integer.valueOf(result);
        }
        if (long.class == shardingPropertiesConstant.getType()) {
            return (T) Long.valueOf(result);
        }
        return (T) result;
    }

}
