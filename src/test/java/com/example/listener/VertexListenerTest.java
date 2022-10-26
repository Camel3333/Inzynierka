package com.example.listener;

import com.example.controller.GraphController;
import com.example.model.MyVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class VertexListenerTest {
    @Mock
    GraphController graphController;

    @Mock
    MyVertex<Integer> vertex;

    VertexListener vertexListener;

    @Test
    public void doubleTraitorAddTest() {
        // Given
        BooleanProperty traitorProperty = new SimpleBooleanProperty(false);
        given(vertex.getIsTraitor()).willReturn(traitorProperty);

        vertexListener = new VertexListener(vertex, graphController);

        // When
        vertexListener.addTraitorListener();
        vertexListener.addTraitorListener();

        // Then no exception should be thrown
    }

    @Test
    public void doubleOpinionAddTest() {
        // Given
        BooleanProperty opinionProperty = new SimpleBooleanProperty(false);
        given(vertex.isSupportingOpinion()).willReturn(opinionProperty);

        vertexListener = new VertexListener(vertex, graphController);

        // When
        vertexListener.addOpinionListener();
        vertexListener.addOpinionListener();

        // Then no exception should be thrown
    }
}
