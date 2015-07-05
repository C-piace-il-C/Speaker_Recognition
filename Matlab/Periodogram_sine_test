a = zeros(1,10);
for n = 1:10
    a(n) = sin(n-1);
end
ham = transpose(hamming(10));
input = (a) .* ham;
fourierTransform = fft( input );
output = abs(fourierTransform) .* abs(fourierTransform) ./ 10;
