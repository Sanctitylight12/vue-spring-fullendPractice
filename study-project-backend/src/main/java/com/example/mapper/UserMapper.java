package com.example.mapper;


import com.example.entity.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserMapper {
    @Select("select * from db_account where username= #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    @Insert("insert into db_account(email, username, password) values(#{email},#{username},#{password})")
    int createAccount( @Param("username") String username,
                       @Param("password") String password,
                       @Param("email")    String email);

}
