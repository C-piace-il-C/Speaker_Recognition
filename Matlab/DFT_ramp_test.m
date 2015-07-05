a = zeros(1,10);
for n = 1:10
    a(n) = n-1;
end
input = a + 0i;
output = fft(input);
