package ru.proshik.applepriceparcer.provider2;

import ru.proshik.applepriceparcer.exception.ProviderParseException;
import ru.proshik.applepriceparcer.model2.Fetch;

public interface Provider2 {

    Fetch screening() throws ProviderParseException;
}
