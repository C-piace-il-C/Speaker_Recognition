sequence = zeros(1,8000);
for n=1:8000
    sequence(n) = sin(n/80);
end
MFCC = extractMFCC(sequence);