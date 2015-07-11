/**
 * Created by doddo on 7/11/15.
 */
public class Threaded_FE{
    Thread firstThread = new Thread()
    {
        @Override
        public void run()
        {
            for(int i = 0; i < 10000; i++);
        }
    };

}
