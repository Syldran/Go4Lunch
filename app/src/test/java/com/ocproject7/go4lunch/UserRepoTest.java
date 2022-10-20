package com.ocproject7.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.ocproject7.go4lunch.data.repositories.UserRepository;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UserRepoTest {
    @Mock
    private CollectionReference mockedCollection;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseUser firebaseUser;

//    @Spy
//    UserRepository userRepository;

    UserRepository userRepository;

    @Mock
    DocumentReference mDocumentReference;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userRepository = UserRepository.getInstance();
    }

    @Test
    public void testRepoGetCurrentUserSuccess(){
        // given
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);

        // when
        FirebaseUser firebaseUser1 = userRepository.getCurrentUser();
        verify(firebaseAuth.getCurrentUser());

        // then
        assertNotNull(firebaseUser1);
        assertEquals(firebaseUser, firebaseUser1);

    }

    @Test
    public void testCreateUser(){
        when(firebaseUser.getUid()).thenReturn("123");
        when(firebaseUser.getPhotoUrl()).thenReturn(null);
        when(firebaseUser.getDisplayName()).thenReturn("name");
        when(firebaseUser.getEmail()).thenReturn("name@test.fr");
        when(mockedCollection.document(anyString())).thenReturn(mDocumentReference);

        //when
        User userToCreate = new User("123", "name", "name@test.fr", null, null, null);
        userRepository.createUser(firebaseUser, mockedCollection);
        verify(mDocumentReference).set(any(User.class));
    }

    @Test
    public void testGetUserData(){
//        when(userRepository.getUsersCollection()).thenReturn(mockedCollection);

    }

    @Test
    public void testRepoGetCurrentUserFailure(){
        // given

        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
        when(FirebaseAuth.getInstance()).thenReturn(firebaseAuth);
        when(firebaseAuth.getCurrentUser()).thenReturn(null);

        // when
        FirebaseUser firebaseUser1 = userRepository.getCurrentUser();

        // then
        assertNull(firebaseUser1);
    }
}
