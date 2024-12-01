package com.example.trojan0project.organizerUnitTests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class Organizer5CancelEntrants {

    private CancelEntrants cancelEntrants;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private DocumentReference mockEventDocument;
    @Mock
    private CollectionReference mockUserCollection;
    @Mock
    private Query mockQuery;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private WriteBatch mockBatch;

    private final String targetEventId = "Tm6SgOQNJgwcy79chggL";
    private final Date pastDeadline = new Date(System.currentTimeMillis() - 86400000); // Yesterday
    private final Date futureDeadline = new Date(System.currentTimeMillis() + 86400000); // Tomorrow

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        cancelEntrants = Robolectric.buildActivity(CancelEntrants.class).create().get();
        cancelEntrants.db = mockFirestore;

        when(mockFirestore.collection("events")).thenReturn(mock(CollectionReference.class));
        when(mockFirestore.collection("users")).thenReturn(mockUserCollection);
        when(mockFirestore.batch()).thenReturn(mockBatch);
    }

    @Test
    public void testGetDeadlineSuccess() {
        when(mockEventDocument.get()).thenReturn(mock(Task.class));
        when(mockFirestore.collection("events").document(targetEventId)).thenReturn(mockEventDocument);

        cancelEntrants.getDeadline();

        verify(mockEventDocument).get(); // Ensure Firestore `get()` is called
    }

    @Test
    public void testCancelEntrants_DeadlinePassed() {
        // Mock entrant data
        Map<String, Long> eventsMap = new HashMap<>();
        eventsMap.put(targetEventId, 1L); // Status 1: Invited to apply

        QueryDocumentSnapshot mockUserSnapshot = mock(QueryDocumentSnapshot.class);
        when(mockUserSnapshot.getString("user_type")).thenReturn("entrant");
        when(mockUserSnapshot.get("events")).thenReturn(eventsMap);

        // Simulate query result
        when(mockQuerySnapshot.iterator()).thenReturn(List.of(mockUserSnapshot).iterator());
        when(mockUserCollection.get()).thenReturn(mock(Task.class));
        when(mock(Task.class).getResult()).thenReturn(mockQuerySnapshot);

        // Set deadline
        cancelEntrants.signupDeadline = pastDeadline;

        cancelEntrants.cancelEntrants();

        // Verify Firestore updates
        verify(mockBatch).update(mockUserSnapshot.getReference(), "events", eventsMap);
        verify(mockBatch).commit();
    }

}
