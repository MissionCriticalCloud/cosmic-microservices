package com.github.missioncriticalcloud.cosmic.usage.core.model;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_SCALE;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.NetworkingView;

public class PublicIp extends Resource {

    @JsonView({DetailedView.class, NetworkingView.class})
    private String value;

    @JsonView({DetailedView.class, NetworkingView.class})
    private State state;

    @JsonView({DetailedView.class, NetworkingView.class})
    private BigDecimal duration;

    @JsonIgnore
    private Network network;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public BigDecimal getDuration() {
        return duration.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public void setDuration(final BigDecimal amount) {
        this.duration = amount;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(final Network network) {
        this.network = network;
    }

    public enum State {

        ATTACHED("Attached"),
        DETACHED("Detached");

        private final String value;

        State(final String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

    }
}
