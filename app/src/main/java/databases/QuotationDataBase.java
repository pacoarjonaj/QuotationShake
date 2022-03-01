package databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import model.Quotation;

@Database(entities = {Quotation.class}, version = 1)
public class QuotationDataBase extends RoomDatabase {

    private static QuotationDataBase quotationDataBase;

    public synchronized static QuotationDataBase getInstance(Context context){
        if(quotationDataBase == null){
            quotationDataBase = Room.databaseBuilder(context,QuotationDataBase.class, "quotation_database")
                                    .allowMainThreadQueries()
                                    .build();
        }
        return quotationDataBase;
    }

    public abstract QuotationDAO quotationDAO();
}
