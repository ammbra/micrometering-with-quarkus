package org.acme.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import java.util.Locale;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RegisterForReflection
public class MessageDTO {

    private Locale locale;

    private String content;
}
