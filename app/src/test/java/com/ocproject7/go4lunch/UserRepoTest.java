package com.ocproject7.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.ocproject7.go4lunch.data.repositories.UserRepository;
import com.ocproject7.go4lunch.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({ FirebaseAuth.class, FirebaseApp.class})
public class UserRepoTest {
    @Mock
    private CollectionReference mockedCollection;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseUser firebaseUser;

    @Spy
    UserRepository userRepository;

    @Mock
    DocumentReference mDocumentReference;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        userRepository = new UserRepository();
    }

    @Test
    public void testRepoGetCurrentUserSuccess(){
        // given

//
//        try (MockedStatic<FirebaseAuth> firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth.class)) {
//            firebaseAuthMockedStatic.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
//            assertEquals(FirebaseAuth.getInstance(), firebaseAuth);
//        }


//        Context context = PowerMockito.mock(Context.class);
//        FirebaseApp firebaseApp = mock(FirebaseApp.class);
//        firebaseApp.initializeApp(context);

//        PowerMockito.mockStatic(FirebaseAuth.class);
//        PowerMockito.when(FirebaseAuth.getInstance()).thenReturn(firebaseAuth);
//
//        FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
//        assertEquals(firebaseApp, FirebaseApp.getInstance());

//        firebaseAuth = FirebaseAuth.getInstance();
//        assertEquals(firebaseAuth, FirebaseAuth.getInstance());
//
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
//
//
        // when
        FirebaseUser firebaseUser1 = userRepository.getCurrentUser();
        verify(firebaseAuth.getCurrentUser());

        // then
        assertNotNull(firebaseUser1);
        assertEquals(firebaseUser, firebaseUser1);

    }

    @Test
    public void testCreateUser(){
        doReturn(firebaseUser).when(userRepository).getCurrentUser();
        when(firebaseUser.getUid()).thenReturn("123");
        when(firebaseUser.getPhotoUrl()).thenReturn(null);
        when(firebaseUser.getDisplayName()).thenReturn("name");
        when(firebaseUser.getEmail()).thenReturn("name@test.fr");
//        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        when(userRepository.getUsersCollection()).thenReturn(mockedCollection);
        when(mockedCollection.document(any())).thenReturn(mDocumentReference);

        //when

        User userToCreate = new User("123", "name", "name@test.fr", null, null, null);
//        verify(mockedCollection)
//                .document("123");
//                .set(userToCreate));
        userRepository.createUser();
        verify(firebaseUser).getEmail();
        //then
//        assertEquals();
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
