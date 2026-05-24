package com.abajin.innovation.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TicketStatusEnum 的 MyBatis TypeHandler
 * 数据库 status_id 列存储整数 code（1-5）
 */
@MappedTypes(TicketStatusEnum.class)
public class TicketStatusEnumTypeHandler extends BaseTypeHandler<TicketStatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TicketStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public TicketStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return TicketStatusEnum.fromCode(code);
    }

    @Override
    public TicketStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return TicketStatusEnum.fromCode(code);
    }

    @Override
    public TicketStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return TicketStatusEnum.fromCode(code);
    }
}
