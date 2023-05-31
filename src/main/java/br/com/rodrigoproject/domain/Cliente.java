package br.com.rodrigoproject.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * A Cliente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cliente", uniqueConstraints = @UniqueConstraint(columnNames = "cpf", name = "uk_cliente_cpf"))
@Document(indexName = "cliente")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cliente extends Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;
}
