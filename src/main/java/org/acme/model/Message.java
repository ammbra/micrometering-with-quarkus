package org.acme.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Locale;

@Getter
@Setter
@Entity
@ToString
@RegisterForReflection
public class Message {
    @Id
    @SequenceGenerator(name = "messageSeq", sequenceName = "message_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "messageSeq")
    private Long id;

    @Basic(optional = false)
    private Locale locale;

    private String content;


}