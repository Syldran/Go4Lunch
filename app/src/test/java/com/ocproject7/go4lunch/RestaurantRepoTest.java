package com.ocproject7.go4lunch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.ocproject7.go4lunch.data.PlacesApi;
import com.ocproject7.go4lunch.data.callback.OnDetailsRestaurant;
import com.ocproject7.go4lunch.data.callback.OnGetRestaurants;
import com.ocproject7.go4lunch.data.repositories.RestaurantRepository;
import com.ocproject7.go4lunch.data.responses.DetailsResponse;
import com.ocproject7.go4lunch.data.responses.NearbyResponse;
import com.ocproject7.go4lunch.models.Restaurant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

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
    OnDetailsRestaurant mockedOnDetailsRestaurants;

    @Mock
    Call<NearbyResponse> mNearbyResponseCall;

    @Mock
    Call<DetailsResponse> mDetailsResponseCall;

    @Mock
    NearbyResponse mNearbyResponse;

    @Mock
    DetailsResponse mDetailsResponse;

    RestaurantRepository restaurantRepository;

    @Captor
    private ArgumentCaptor<List<Restaurant>> restaurantsCaptor;
    @Captor
    private ArgumentCaptor<Restaurant> restoCaptor;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        restaurantRepository = new RestaurantRepository(mockedPlaces);
    }

    @Test
    public void testGetRestaurants(){
        List<Restaurant> restoList = Arrays.asList(new Restaurant(), new Restaurant());

        Response<NearbyResponse> response = Response.success((mNearbyResponse));
        when(mockedPlaces.getNearBy(anyString(), anyInt(), anyString(), anyString(), anyString())).thenReturn(mNearbyResponseCall);
        when(mNearbyResponse.getResults()).thenReturn(restoList);
        Mockito.doAnswer(new Answer() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback<NearbyResponse> callback = invocation.getArgument(0, Callback.class);
                callback.onResponse(mNearbyResponseCall, response);
                return null;
            }
        }).when(mNearbyResponseCall).enqueue(any(Callback.class));
        restaurantRepository.getRestaurants(
                "Paris",
                1500,
                "prominence",
                mockedOnGetRestaurants);

        verify(mNearbyResponseCall).enqueue(any());
        verify(mockedOnGetRestaurants).onGetRestaurantData(restaurantsCaptor.capture());
        List<Restaurant> restaurantList = restaurantsCaptor.getValue();
        assertEquals(restaurantList.size(), 2);
    }

    @Test
    public void testGetDetailsRestaurant(){
        Restaurant resto = new Restaurant();
        resto.setPlaceId("Paris");

        Response<DetailsResponse> response = Response.success((mDetailsResponse));
        when(mockedPlaces.getDetails(anyString(), anyString())).thenReturn(mDetailsResponseCall);

        when(mDetailsResponse.getResult()).thenReturn(resto);

        Mockito.doAnswer(new Answer() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback<DetailsResponse> callback = invocation.getArgument(0, Callback.class);

                callback.onResponse(mDetailsResponseCall, response);

                return null;
            }
        }).when(mDetailsResponseCall).enqueue(any(Callback.class));
        restaurantRepository.getDetailsRestaurant("Paris", mockedOnDetailsRestaurants);

        verify(mDetailsResponseCall).enqueue(any());
        verify(mockedOnDetailsRestaurants).onGetDetailsRestaurantData(restoCaptor.capture());
        Restaurant restaurant = restoCaptor.getValue();
        assertNotNull(restaurant);
        assertEquals(restaurant.getPlaceId(), "Paris");
    }
}
