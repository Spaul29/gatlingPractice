package videogamedb;

import java.util.Map;

public class Headers {
    static Map<CharSequence, String> authorizationHeaders = Map.ofEntries(
            Map.entry("authorization", "Bearer #{jwt}")
    );
}
