function DD = extractDD( CC, M )
d = size(CC);
frame_count = d(1);
feature_count = d(2);

DD = zeros(frame_count,feature_count);

divisore = (2 * (M * (M + 1) * (2 * M + 1)) / 6.0)^2;
for f=1:frame_count
    for k=1:feature_count
        for m=-M:M
            for n=-M:M
                if(f+m+n > 0 && f+m+n < frame_count)
                    DD(f,k) = DD(f,k) + m*n*CC(f+m+n,k);
                end
            end
        end
        DD(f,k) = DD(f,k) / divisore;
    end
end
end

