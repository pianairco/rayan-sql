package ir.piana.rayan.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by mj.rahmati on 1/2/2020.
 */
@Entity
@Table(name = "TestTable")
public class TestTableEntity {
    private Long id;

    private String columnA;

    private int columnB;

    private boolean columnC;

    private Timestamp columnD;

    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "column_a")
    public String getColumnA() {
        return columnA;
    }

    public void setColumnA(String columnA) {
        this.columnA = columnA;
    }

    @Column(name = "column_b")
    public int getColumnB() {
        return columnB;
    }

    public void setColumnB(int columnB) {
        this.columnB = columnB;
    }

    @Column(name = "column_c")
    public boolean isColumnC() {
        return columnC;
    }

    public void setColumnC(boolean columnC) {
        this.columnC = columnC;
    }

    @Column(name = "column_d")
    public Timestamp getColumnD() {
        return columnD;
    }

    public void setColumnD(Timestamp columnD) {
        this.columnD = columnD;
    }
}
