package ru.practicum.explorewithme.util;

import ru.practicum.explorewithme.error.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


public class SizeRequest extends PageRequest {
    private final long offset;

    private SizeRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        offset = from;
    }

    public static SizeRequest from(int from, int size) {
        isCorrectOr400Error(from, size);
        return new SizeRequest(from, size, Sort.unsorted());
    }

    public static SizeRequest from(int from, int size, Sort sort) {
        isCorrectOr400Error(from, size);
        return new SizeRequest(from, size, sort);
    }

    private static void isCorrectOr400Error(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadRequestException("No positive values in the pagination");
        }
    }

    @Override
    public long getOffset() {
        return offset;
    }
}
