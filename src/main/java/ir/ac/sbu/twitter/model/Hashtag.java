package ir.ac.sbu.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name="HASHTAGS7")
@EqualsAndHashCode(of = "ID")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BODY", unique = true)
    private String body;

    @Override
    public boolean equals(Object o){
        if(o instanceof Hashtag)
            return ((Hashtag) o).getBody().equals(body);
        return false;
    }
}
