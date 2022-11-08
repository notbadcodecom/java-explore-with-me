package com.notbadcode.explorewithme.util;

import com.notbadcode.explorewithme.error.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


public class FromSizeRequest extends PageRequest {
    private final long offset;

    private FromSizeRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
        offset = from;
    }

    public static FromSizeRequest of(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadRequestException("No positive values in the pagination");
        }
        return new FromSizeRequest(from, size);
    }

    @Override
    public long getOffset() {
        return offset;
    }
}