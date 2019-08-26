package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.text.MessageFormat;
import java.util.List;

@Repository
public class CustomerDao {

    public static final String TENANT_SQL_TEMPLATE = "/*!mycat:schema={0} */{1}";
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        String tenantSchema = ThreadLocalUtil.getTenant();
        String sql = MessageFormat.format(TENANT_SQL_TEMPLATE, tenantSchema, "select * from customer");
        System.out.println(sql);
        return jdbcTemplate.query(sql, new CustomerRowMapper());
    }

    @Transactional(readOnly = true)
    public Customer findUserById(int id) {
        String tenantSchema = ThreadLocalUtil.getTenant();
        return jdbcTemplate.queryForObject(MessageFormat.format(TENANT_SQL_TEMPLATE, tenantSchema, "select * from customer where id=?"), new Object[]{id}, new CustomerRowMapper());
    }

    @Transactional
    public Customer create(Customer customer) {
        String tenantSchema = ThreadLocalUtil.getTenant();
        final String sql = MessageFormat.format(TENANT_SQL_TEMPLATE, tenantSchema, "insert into customer(id,name) values(?,?)");

        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, customer.getName());
                return ps;
            }
        }, holder);

        int newUserId = holder.getKey().intValue();
        customer.setId(newUserId);
        return customer;
    }

    @Transactional
    public void delete(Integer id) {
        String tenantSchema = ThreadLocalUtil.getTenant();
        final String sql = MessageFormat.format(TENANT_SQL_TEMPLATE, tenantSchema, "delete from customer where id=?");
        jdbcTemplate.update(sql, new Object[]{id}, new int[]{java.sql.Types.INTEGER});
    }

    @Transactional
    public void update( final Customer user) {
        String tenantSchema = ThreadLocalUtil.getTenant();
        jdbcTemplate.update(MessageFormat.format(TENANT_SQL_TEMPLATE, tenantSchema, "update customer set name=? where id=?"),
                new Object[]{user.getName(), user.getId()});
    }

    class CustomerRowMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer c = new Customer();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            return c;
        }

    }
}