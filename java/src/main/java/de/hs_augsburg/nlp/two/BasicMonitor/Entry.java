package de.hs_augsburg.nlp.two.BasicMonitor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Entry {
    private final String text;
    private final int amount;
    private final EntryType type;

    public Entry(String text, int amount, EntryType type) {
        this.text = text;
        this.amount = amount;
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("text", text)
                .append("amount", amount)
                .append("type", type)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        return new EqualsBuilder()
                .append(amount, entry.amount)
                .append(text, entry.text)
                .append(type, entry.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .append(amount)
                .append(type)
                .toHashCode();
    }
}
