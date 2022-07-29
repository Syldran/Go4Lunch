package com.ocproject7.go4lunch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocproject7.go4lunch.data.callback.OnDetailsRestaurant;
import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.data.repositories.UserRepository;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;

import java.util.Objects;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    private static final String TAG = "TAG_ExampleUnitTest";

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private Task<DocumentSnapshot> documentSnapshotTask;

    @Mock
    CollectionReference mCollectionReference;

    @Mock
    private Task<QuerySnapshot> querySnapshotTask;

    RestaurantViewModel restaurantViewModel;

    User userTest;
    Restaurant restaurantTest;


    ArgumentCaptor<OnCompleteListener> testOnCompleteListener = ArgumentCaptor.forClass(OnCompleteListener.class);
    ArgumentCaptor<OnSuccessListener> testOnSuccessListener = ArgumentCaptor.forClass(OnSuccessListener.class);
    ArgumentCaptor<OnFailureListener> testOnFailureListener = ArgumentCaptor.forClass(OnFailureListener.class);
    ArgumentCaptor<EventListener<DocumentSnapshot>> eventSnapshotListener = ArgumentCaptor.forClass(EventListener.class);
    <T> void setupTask(Task<T> task) {
        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
        when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
        when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
    }

    @Before
    public void setUp(){
        restaurantViewModel = new RestaurantViewModel(restaurantRepository, userRepository);
        setupTask(documentSnapshotTask);


        restaurantTest = new Restaurant();
    }

    @Test
    public void testCurrentUserLoggedFailWhenFirebaseUserDoesNotExist(){
        // Given
        when(userRepository.getCurrentUser()).thenReturn(null);

        // When
        boolean isLogged = restaurantViewModel.isCurrentUserLogged();

        // Then
        assertFalse(isLogged);
    }

    @Test
    public void testCurrentUserLoggedSucceedWhenFirebaseUserExist(){
        // Given
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);

        // When
        boolean isLogged = restaurantViewModel.isCurrentUserLogged();

        // Then
        assertTrue(isLogged);
    }


    @Test
    public void testGetUserData(){
        // Given

        String uid = "ChIJHdAimNiV-kcRIXqC5dQDU5w";
//        when(userRepository.getUserData(uid)).thenReturn(documentSnapshotTask);
//        testOnSuccessListener.getValue();
        Answer<DocumentSnapshot> answer = new Answer<DocumentSnapshot>() {
            @Override
            public DocumentSnapshot answer(InvocationOnMock invocation) throws Throwable {
                DocumentSnapshot documentSnapshot = invocation.getArgument(0);
                return documentSnapshot;
            }
        };
        doAnswer(answer).when(userRepository.getUserData(uid));
        // When
        restaurantViewModel.updateUserFromFirestore(uid);

        // Then
        assertNotNull(restaurantViewModel.currentUser.getValue());

    }

//        @Test
//    public void testUpdateRestaurant() {
//        //given
//        String id = "ChIJHdAimNiV-kcRIXqC5dQDU5w";
//        String name = "La Scaleta";
//        when(userRepository.getUsersCollection()).thenReturn();
//
//        //when
//        restaurantViewModel.updateRestaurant(id, name);
//
//        //then
//        assertNotNull(restaurantViewModel.mDetails);
//    }

    @Test
    public void testFetchDetailsRestaurant() {
        //given
        OnDetailsRestaurant onDetailsRestaurant = mock(OnDetailsRestaurant.class);
        String id = "ChIJHdAimNiV-kcRIXqC5dQDU5w";

        Mockito.doNothing().when(restaurantRepository).getDetailsRestaurant(id, onDetailsRestaurant);

        //when
        restaurantViewModel.fetchDetailsRestaurant(id);

        //then
        assertNotNull(Objects.requireNonNull(restaurantViewModel.mDetail.getValue()));
    }

    //
    @Test
    public void testGetAllUsers() {
        when(userRepository.getUsersCollection().get()).thenReturn(querySnapshotTask);
        when(querySnapshotTask.getResult().size()).thenReturn(2);

        //when
        restaurantViewModel.getUsers();

        //then
        assertEquals(Objects.requireNonNull(restaurantViewModel.allUsers.getValue()).size(), 2);

    }
//
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
}