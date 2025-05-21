package kr.rth.picoserver.etc;

import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class consoleFilter implements Filter {
    public static ArrayList<String> blockedMessage = new ArrayList<>();

    public consoleFilter() {
//        this.blockedMessage = blockedMessage;
        blockedMessage.add("java.util.ConcurrentModificationException: null");
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        // 원하는 조건에 따라 로그를 필터링할 수 있습니다.
        // 여기서는 특정 메시지를 포함하는 로그를 차단하는 예제를 보여줍니다.
        return true;
//        for(String i : blockedMessage) {
//            if(record.getMessage().contains(i))  {
//                return false;
//            }
////            return !record.getMessage().contains(i);
//
//        }
//        return true;
    }
}
