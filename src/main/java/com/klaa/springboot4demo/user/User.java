package com.klaa.springboot4demo.user;

import com.klaa.springboot4demo.jpa.AbstractJpaVersionedAuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=false)
@Table(name = "users")
public class User extends AbstractJpaVersionedAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String  email;
}
