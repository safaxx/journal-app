
package com.portfolio.journalApp.service;

import com.portfolio.journalApp.entity.User;
import com.portfolio.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock  // Add this mock for passwordEncoder
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSaveUserInfo(){
        String username = "test.user";
        String password = "test@password";
        String encodedPassword = "encoded@password";

        User testUser = new User();
        testUser.setUsername(username);
        testUser.setPassword(password);

        User savedUser = new User();
        savedUser.setUsername(username);
        savedUser.setPassword(encodedPassword);
        savedUser.setRoles(List.of("USER"));


        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = service.saveUserInfo(testUser);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getRoles()).contains("USER");

        verify(passwordEncoder).encode(password);
        verify(repository).save(any(User.class));
    }

    @Test
    void testFindAllUser(){
        List<User> users = new ArrayList<>();
        users.add(new User("1", "username1", "password1", List.of("USER"), new ArrayList<>()));
        users.add(new User("2", "username2", "password2", List.of("USER"), new ArrayList<>()));

        when(repository.findAll()).thenReturn(users);
        List<User> result = service.findAllUsers();
        assertThat(result).hasSize(2);
        verify(repository).findAll();

    }

    @Test
    void testFindByUsername(){
        User user =(new User("1", "username1", "password1", List.of("USER"), new ArrayList<>()));

        when(repository.findByUsername("username1")).thenReturn(user);
        User result = service.findUser("username1");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

}
