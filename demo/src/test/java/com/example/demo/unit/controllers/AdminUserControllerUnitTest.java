package com.example.demo.unit.controllers;

import com.example.demo.AdminUserController;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminUserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Test for listUsers ---
    @Test
    void testListUsers() {
        // Arrange: simulate two users.
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        ClassSignUp signup1 = new ClassSignUp();
        signup1.setSchoolClassId(10L);
        when(classSignUpRepository.findByUserId(1L)).thenReturn(Arrays.asList(signup1));
        when(classSignUpRepository.findByUserId(2L)).thenReturn(Collections.emptyList());

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(10L);
        schoolClass.setName("Math");
        when(schoolClassRepository.findById(10L)).thenReturn(Optional.of(schoolClass));

        String view = controller.listUsers(model);

        verify(model).addAttribute("users", users);

        ArgumentCaptor<Map<Long, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("userClassesMap"), mapCaptor.capture());
        Map<Long, String> userClassesMap = mapCaptor.getValue();
        assertEquals("Math", userClassesMap.get(1L));
        assertEquals("", userClassesMap.get(2L));

        assertEquals("adminUsers", view);
    }

    @Test
    void testShowAddUserForm() {
        SchoolClass class1 = new SchoolClass();
        class1.setId(10L);
        class1.setName("Math");
        SchoolClass class2 = new SchoolClass();
        class2.setId(20L);
        class2.setName("History");
        Iterable<SchoolClass> classIterable = Arrays.asList(class1, class2);
        when(schoolClassRepository.findAll()).thenReturn(classIterable);

        // Act
        String view = controller.showAddUserForm(model);
        verify(model).addAttribute(eq("user"), any(User.class));
        ArgumentCaptor<List<SchoolClass>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("classes"), listCaptor.capture());
        List<SchoolClass> classes = listCaptor.getValue();
        assertEquals(2, classes.size());
        assertEquals("Math", classes.get(0).getName());
        assertEquals("History", classes.get(1).getName());
        assertEquals("adminUserForm", view);
    }

    @Test
    void testAddUserWithoutClassIds() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("john");
        newUser.setEmail("john@example.com");
        doAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        }).when(userRepository).save(newUser);

        List<Long> classIds = null;

        String view = controller.addUser(newUser, classIds, model);

        verify(model).addAttribute(eq("errorMessage"), contains("john"));
        verify(userRepository).save(newUser);
        verify(classSignUpRepository, never()).save(any(ClassSignUp.class));
        assertEquals("redirect:/admin/users", view);
    }

    @Test
    void testAddUserWithClassIds() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("alice");
        newUser.setEmail("alice@example.com");

        doAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        }).when(userRepository).save(newUser);

        List<Long> classIds = Arrays.asList(10L, 20L);

        String view = controller.addUser(newUser, classIds, model);

        verify(userRepository).save(newUser);

        ArgumentCaptor<ClassSignUp> signupCaptor = ArgumentCaptor.forClass(ClassSignUp.class);
        verify(classSignUpRepository, times(2)).save(signupCaptor.capture());
        List<ClassSignUp> signups = signupCaptor.getAllValues();
        for (ClassSignUp cs : signups) {
            assertEquals(2L, cs.getUserId());
            assertTrue(classIds.contains(cs.getSchoolClassId()));
            assertEquals("APPROVED", cs.getStatus());
            assertNotNull(cs.getCreatedDate());
        }
        assertEquals("redirect:/admin/users", view);
    }

    @Test
    void testShowEditUserForm_UserFound() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("bob");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        SchoolClass class1 = new SchoolClass();
        class1.setId(10L);
        class1.setName("Math");
        Iterable<SchoolClass> classIterable = Arrays.asList(class1);
        when(schoolClassRepository.findAll()).thenReturn(classIterable);

        ClassSignUp signup = new ClassSignUp();
        signup.setSchoolClassId(10L);
        when(classSignUpRepository.findByUserId(userId)).thenReturn(Arrays.asList(signup));

        String view = controller.showEditUserForm(userId, model);

        verify(model).addAttribute("user", user);
        ArgumentCaptor<List<SchoolClass>> classesCaptor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("classes"), classesCaptor.capture());
        List<SchoolClass> classes = classesCaptor.getValue();
        assertEquals(1, classes.size());
        assertEquals("Math", classes.get(0).getName());

        ArgumentCaptor<List<Long>> assignedCaptor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("assignedClasses"), assignedCaptor.capture());
        List<Long> assignedClasses = assignedCaptor.getValue();
        assertEquals(1, assignedClasses.size());
        assertEquals(10L, assignedClasses.get(0));

        assertEquals("adminUserForm", view);
    }

    @Test
    void testShowEditUserForm_UserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String view = controller.showEditUserForm(userId, model);

        assertEquals("redirect:/admin/users", view);
    }

    @Test
    void testEditUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldname");
        existingUser.setEmail("old@example.com");
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");
        existingUser.setRole("USER");
        existingUser.setPassword("oldPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        User updatedUser = new User();
        updatedUser.setUsername("newname");
        updatedUser.setEmail("new@example.com");
        updatedUser.setFirstName("NewFirst");
        updatedUser.setLastName("NewLast");
        updatedUser.setRole("ADMIN");
        updatedUser.setPassword("newPassword");
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        ClassSignUp signupExisting = new ClassSignUp();
        signupExisting.setSchoolClassId(10L);
        when(classSignUpRepository.findByUserId(userId)).thenReturn(Arrays.asList(signupExisting));
        when(classSignUpRepository.findBySchoolClassIdAndUserId(20L, userId)).thenReturn(Optional.empty());
        when(classSignUpRepository.findBySchoolClassIdAndUserId(10L, userId))
                .thenReturn(Optional.of(signupExisting));

        List<Long> newClassIds = Arrays.asList(20L);

        String view = controller.editUser(userId, updatedUser, newClassIds);

        assertEquals("newname", existingUser.getUsername());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("NewFirst", existingUser.getFirstName());
        assertEquals("NewLast", existingUser.getLastName());
        assertEquals("ADMIN", existingUser.getRole());
        assertEquals("encodedPassword", existingUser.getPassword());
        verify(userRepository).save(existingUser);

        verify(classSignUpRepository).delete(signupExisting);
        ArgumentCaptor<ClassSignUp> signupCaptor = ArgumentCaptor.forClass(ClassSignUp.class);
        verify(classSignUpRepository).save(signupCaptor.capture());
        ClassSignUp newSignup = signupCaptor.getValue();
        assertEquals(userId, newSignup.getUserId());
        assertEquals(20L, newSignup.getSchoolClassId());
        assertEquals("APPROVED", newSignup.getStatus());
        assertNotNull(newSignup.getCreatedDate());
        assertEquals("redirect:/admin/users", view);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        String view = controller.deleteUser(userId);
        verify(userRepository).deleteById(userId);
        assertEquals("redirect:/admin/users", view);
    }
}
