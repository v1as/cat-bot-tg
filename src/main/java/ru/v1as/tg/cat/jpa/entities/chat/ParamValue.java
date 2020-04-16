package ru.v1as.tg.cat.jpa.entities.chat;

import static java.lang.Integer.parseInt;
import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = TABLE_PER_CLASS)
public abstract class ParamValue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @ManyToOne(optional = false)
    protected ChatEntity chat;

    @Column(nullable = false, length = 20)
    protected String value;

    public Boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }

    public int getInt() {
        return parseInt(value);
    }

    public void setValue(Object value) {
        this.value = value == null || value instanceof String ? (String) value : value.toString();
    }
}
