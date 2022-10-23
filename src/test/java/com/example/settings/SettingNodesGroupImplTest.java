package com.example.settings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SettingNodesGroupImplTest {
    @Mock
    SettingNode<Integer> settingNode1;
    @Mock
    SettingNode<Integer> settingNode2;

    SettingNodesGroupImpl settingNodesGroup;

    @Test
    public void propertyShouldBeValid() {
        // Given
        BooleanProperty booleanProperty1 = new SimpleBooleanProperty(true);
        BooleanProperty booleanProperty2 = new SimpleBooleanProperty(true);
        given(settingNode1.getIsValidProperty()).willReturn(booleanProperty1);
        given(settingNode2.getIsValidProperty()).willReturn(booleanProperty2);
        settingNodesGroup = new SettingNodesGroupImpl(List.of(settingNode1, settingNode2));

        // When
        BooleanProperty returnedProperty = settingNodesGroup.getAreAllValidProperty();

        // Then
        assertThat(returnedProperty.get()).isTrue();
    }

    @Test
    public void propertyShouldBeInvalid() {
        // Given
        BooleanProperty booleanProperty1 = new SimpleBooleanProperty(false);
        BooleanProperty booleanProperty2 = new SimpleBooleanProperty(true);
        given(settingNode1.getIsValidProperty()).willReturn(booleanProperty1);
        given(settingNode2.getIsValidProperty()).willReturn(booleanProperty2);
        settingNodesGroup = new SettingNodesGroupImpl(List.of(settingNode1, settingNode2));

        // When
        BooleanProperty returnedProperty = settingNodesGroup.getAreAllValidProperty();

        // Then
        assertThat(returnedProperty.get()).isFalse();
    }

    @Test
    public void shouldReturnTrueForEmptyList() {
        // Given
        List<SettingNode<?>> emptySettingsList = new ArrayList<>();
        settingNodesGroup = new SettingNodesGroupImpl(emptySettingsList);

        // When
        BooleanProperty returnedProperty = settingNodesGroup.getAreAllValidProperty();

        // Then
        assertThat(returnedProperty.get()).isTrue();
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNullIsPassed() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SettingNodesGroupImpl(null);
        });
    }
}
