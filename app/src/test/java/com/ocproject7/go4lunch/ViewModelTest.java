package com.ocproject7.go4lunch;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.ocproject7.go4lunch.data.callback.OnDetailsRestaurant;
import com.ocproject7.go4lunch.data.callback.OnGetRestaurants;
import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.data.repositories.UserRepository;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.models.User;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewModelTest {

    private static final String TAG = "TAG_ExampleUnitTest";


    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private DocumentSnapshot documentSnapshot1;

    @Mock
    private Task<DocumentSnapshot> documentSnapshotTask;

    @Mock
    CollectionReference mCollectionReference;

    @Mock
    DocumentReference mDocumentReference;

    @Mock
    private Task<QuerySnapshot> querySnapshotTask;

    @Mock
    private QuerySnapshot mQuerySnapshot;

    RestaurantViewModel restaurantViewModel;

    Restaurant restaurantTest;


    ArgumentCaptor<OnCompleteListener> testOnCompleteListener = ArgumentCaptor.forClass(OnCompleteListener.class);
    ArgumentCaptor<OnSuccessListener> testOnSuccessListener = ArgumentCaptor.forClass(OnSuccessListener.class);
    ArgumentCaptor<OnFailureListener> testOnFailureListener = ArgumentCaptor.forClass(OnFailureListener.class);
    ArgumentCaptor<EventListener<DocumentSnapshot>> eventSnapshotListener = ArgumentCaptor.forClass(EventListener.class);
    //    ArgumentCaptor<EventListener<CollectionReference>> eventCollectionListener = ArgumentCaptor.forClass(EventListener.class);
    <T> void setupTask(Task<T> task) {
        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
        when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
//        when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        restaurantViewModel = new RestaurantViewModel(restaurantRepository, userRepository);
        setupTask(documentSnapshotTask);
        setupTask(querySnapshotTask);


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
        when(userRepository.getUserData(uid)).thenReturn(documentSnapshotTask);
        when(documentSnapshotTask.getResult()).thenReturn(documentSnapshot);
        when(documentSnapshot.toObject(User.class)).thenReturn(new User("ChIJHdAimNiV-kcRIXqC5dQDU5w", "", "", "", "", ""));

        // When
        restaurantViewModel.updateUserFromFirestore(uid);

        verify(documentSnapshotTask).addOnSuccessListener(testOnSuccessListener.capture());
        OnSuccessListener<DocumentSnapshot> onSuccessListener = testOnSuccessListener.getValue();
        onSuccessListener.onSuccess(documentSnapshotTask.getResult());

        // Then
        assertNotNull(restaurantViewModel.currentUser.getValue());
        assertEquals(restaurantViewModel.currentUser.getValue().getUserId(), uid);
    }

    @Test
    public void testGetAllUsers() {
        List<DocumentSnapshot> listDoc = Arrays.asList(documentSnapshot, documentSnapshot1);
        when(userRepository.getUsersCollection()).thenReturn(mCollectionReference);
        when(mCollectionReference.get()).thenReturn(querySnapshotTask);
        when(querySnapshotTask.getResult()).thenReturn(mQuerySnapshot);
        when(querySnapshotTask.isSuccessful()).thenReturn(true);
        when(mQuerySnapshot.getDocuments()).thenReturn(listDoc);
        when(documentSnapshot.toObject(User.class)).thenReturn(new User("ChIJHdAimNiV-kcRIXqC5dQDU5w", "", "", "", "", ""));
        when(documentSnapshot1.toObject(User.class)).thenReturn(new User("V3sMwnhr6MWh5Bu1OGNQQ6IMss92", "", "", "", "", ""));

        //when
        restaurantViewModel.getUsers();

        verify(querySnapshotTask).addOnCompleteListener(testOnCompleteListener.capture());
        OnCompleteListener<QuerySnapshot> onCompleteListener = testOnCompleteListener.getValue();
        onCompleteListener.onComplete(querySnapshotTask);


        //then
        assertNotNull(restaurantViewModel.allUsers.getValue());
        assertEquals(Objects.requireNonNull(restaurantViewModel.allUsers.getValue()).size(), 2);

    }

    @Test
    public void testFetchDetailsRestaurant() {
        //given
        Restaurant restaurantTest1 = new Restaurant();
        ArgumentCaptor<OnDetailsRestaurant> onDetailsRestaurantCaptor = ArgumentCaptor.forClass(OnDetailsRestaurant.class);
        String id = "ChIJHdAimNiV-kcRIXqC5dQDU5w";
        restaurantTest.setPlaceId(id);


        //when
        restaurantViewModel.fetchDetailsRestaurant(id);
        verify(restaurantRepository).getDetailsRestaurant(anyString(), onDetailsRestaurantCaptor.capture());

        OnDetailsRestaurant captorValue = onDetailsRestaurantCaptor.getValue();
        captorValue.onGetDetailsRestaurantData(restaurantTest);

        //then
        assertNotNull(Objects.requireNonNull(restaurantViewModel.mDetail.getValue()));
        assertEquals(id, restaurantViewModel.mDetail.getValue().getPlaceId());
    }

    @Test
    public void testFetchNearByRestaurants(){
        //given
        Restaurant restaurantTest1 = new Restaurant();
        restaurantTest.setPlaceId("ChIJHdAimNiV-kcRIXqC5dQDU5w");
        restaurantTest1.setPlaceId("kcRIXqC5dQDU5w-ChIJHdAimNiV");
        List<Restaurant> restaurantListTest = new ArrayList<>();
        restaurantListTest.add(restaurantTest);
        restaurantListTest.add(restaurantTest1);
        when(userRepository.getUsersCollection()).thenReturn(null);
        when(userRepository.getUsersCollection()).thenReturn(mCollectionReference);
        when(mCollectionReference.get()).thenReturn(querySnapshotTask);
        ArgumentCaptor<OnGetRestaurants> onGetRestaurantCaptor = ArgumentCaptor.forClass(OnGetRestaurants.class);


        //when
        restaurantViewModel.fetchRestaurants(1500, "prominence");
        verify(restaurantRepository).getRestaurants(anyString(), anyInt(), anyString(), onGetRestaurantCaptor.capture());
        OnGetRestaurants captorValue = onGetRestaurantCaptor.getValue();
        captorValue.onGetRestaurantData(restaurantListTest);

        //then
        assertNotNull(restaurantViewModel.mRestaurants.getValue());
        assertEquals(restaurantListTest.size(), restaurantViewModel.mRestaurants.getValue().size());
    }

    @Test
    public void testGetCurrentUser(){
        FirebaseUser testFirebaseUser = mock(FirebaseUser.class);
        when(userRepository.getCurrentUser()).thenReturn(testFirebaseUser);

        FirebaseUser firebaseUser = restaurantViewModel.getCurrentUser();

        assertNotNull(firebaseUser);
        assertEquals(firebaseUser, testFirebaseUser);

    }

    @Test
    public void testSignout(){
        Context context = mock(Context.class);
        restaurantViewModel.currentUser.setValue(new User());
        assertNotNull(restaurantViewModel.currentUser.getValue());

        when(userRepository.signOut(context)).thenReturn(null);

        restaurantViewModel.signOut(context);
        assertNull(restaurantViewModel.currentUser.getValue());

    }


    @Test
    public void testUpdateRestaurant() {
        //given

        Task<Void> mVoidTask = mock(Task.class);
        Task<Void> mVoidTask1 = mock(Task.class);
        String idUser = "123";
        String idResto = "ChIJHdAimNiV-kcRIXqC5dQDU5w";
        String nameResto = "La Scaleta";
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(restaurantViewModel.getCurrentUser()).thenReturn(firebaseUser);
        when(firebaseUser.getUid()).thenReturn(idUser);
        when(userRepository.getUsersCollection()).thenReturn(mCollectionReference);
        when(mCollectionReference.document(idUser)).thenReturn(mDocumentReference);
        when(mDocumentReference.update(eq("restaurantId"), anyString())).thenReturn(mVoidTask);
        when(mDocumentReference.update(eq("restaurantName"), anyString())).thenReturn(mVoidTask1);

        //when
        restaurantViewModel.updateRestaurant(idResto, nameResto);
        verify(mDocumentReference, times(1)).update("restaurantId", anyString());
        verify(mDocumentReference, times(1)).update("restaurantName", anyString());
//        verify(userRepository).getUsersCollection().document(idUser).update(anyString(), nameResto);


        //then

//        assertEquals(idResto, restaurantViewModel.currentUser.getValue().getRestaurantId());
//        assertEquals(nameResto, restaurantViewModel.currentUser.getValue().getRestaurantName());
    }
}