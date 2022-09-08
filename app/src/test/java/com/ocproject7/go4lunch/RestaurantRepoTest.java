package com.ocproject7.go4lunch;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ocproject7.go4lunch.data.PlacesApi;
import com.ocproject7.go4lunch.data.RetrofitService;
import com.ocproject7.go4lunch.data.callback.OnGetRestaurants;
import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.data.responses.NearbyResponse;
import com.ocproject7.go4lunch.models.Restaurant;
import com.ocproject7.go4lunch.viewmodels.RestaurantViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantRepoTest {

    @Mock
    PlacesApi mockedPlaces;

    @Mock
    OnGetRestaurants mockedOnGetRestaurants;

    @Mock
    RetrofitService mRetrofitService;

    @Mock
    Call<NearbyResponse> mNearbyResponseCall;

//    @Mock
//    Response<NearbyResponse> mNearbyResponse;

    RestaurantRepository restaurantRepository;

//    ArgumentCaptor<OnGetRestaurants> testOnGetRestaurant = ArgumentCaptor.forClass(OnGetRestaurants.class);
    @Captor
    private ArgumentCaptor<Callback<NearbyResponse>> callbackCaptor;
//    <T> void setupTask(Task<T> task) {
//        when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
//    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        restaurantRepository = new RestaurantRepository();
    }

    @Test
    public void testGetRestaurants(){

        when(mRetrofitService.getPlacesApi()).thenReturn(mockedPlaces);
//        when(mockedPlaces.getNearBy("Paris", 1500, "restaurant","prominence", "AIzaSyDCXBbnL9Tw5L_0G6MMtr-F7ibrX-oAx40")).thenReturn(mNearbyResponseCall);
//        when(mockedPlaces.getNearBy(anyString(), anyInt(), anyString(),anyString(), anyString())).thenReturn(mNearbyResponseCall);
//        verify(mNearbyResponseCall).enqueue(callbackCaptor.capture());
//        Callback<NearbyResponse> nearbyCallBack = callbackCaptor.getValue();
//        nearbyCallBack.onResponse(mNearbyResponseCall,);

    }

    /*
    @Test
    public void testApiResponse() {
        ApiInterface mockedApiInterface = Mockito.mock(ApiInterface.class);
        Call<UserNotifications> mockedCall = Mockito.mock(Call.class);

        when(RetrofitService.getPlacesApi()).thenReturn(mockedPlaces);
        when(mockedPlaces.getNearBy("Paris", 1500, "restaurant","prominence", "AIzaSyDCXBbnL9Tw5L_0G6MMtr-F7ibrX-oAx40")).thenReturn(mNearbyResponseCall);



            @Override
            public void onFailure(Call<NearbyResponse> call, Throwable t) {

            }

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback<UserNotifications> callback = invocation.getArgumentAt(0, Callback.class);

                callback.onResponse(mockedCall, Response.success(new UserNotifications()));
                // or callback.onResponse(mockedCall, Response.error(404. ...);
                // or callback.onFailure(mockedCall, new IOException());

                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));

        // inject mocked ApiInterface to your presenter
        // and then mock view and verify calls (and eventually use ArgumentCaptor to access call parameters)
    }*/
}
