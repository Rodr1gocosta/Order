package br.com.rodrigoproject.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * A Tecnico.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tecnico", uniqueConstraints = @UniqueConstraint(columnNames = "cpf", name = "uk_tecnico_cpf"))
@Document(indexName = "tecnico")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tecnico extends Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;
}
