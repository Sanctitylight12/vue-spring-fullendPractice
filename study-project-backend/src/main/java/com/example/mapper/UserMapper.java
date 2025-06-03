package com.example.mapper;


import com.example.entity.auth.Account;
import com.example.entity.user.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.repository.query.Param;

@Mapper
public interface UserMapper {
    @Select("select * from db_account where username= #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    @Select("select * from db_account where username= #{text} or email= #{text}")
    AccountUser findAccountUserByNameOrEmail(String text);

    @Insert("insert into db_account(email, username, password) values(#{email},#{username},#{password})")
    int createAccount( @Param("username") String username,
                       @Param("password") String password,
                       @Param("email")    String email);

    @Update("update db_account set password = #{password} where email= #{email}")
    int resetPasswordByEmail(@Param("email") String email,
                      @Param("password") String password);


}
