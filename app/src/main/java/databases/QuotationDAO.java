package databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import model.Quotation;

@Dao
public interface QuotationDAO {
    @Insert
    public void insertQuote();

    @Delete
    public void deleteQuote();

    @Query("SELECT * FROM Quotation")
    public List<Quotation> getAllQuotations();

    @Query("SELECT * FROM Quotation q WHERE q.quoteText = :text")
    public Quotation getQuote(String text);

    @Query("DELETE FROM Quotation")
    public void deleteAllQuote();
}
