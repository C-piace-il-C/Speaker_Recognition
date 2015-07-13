function CC = extractCC(frame)

%{
 SOME PARAMETERS
%}
triangle_count = 26;
start_freq = 300; 
end_freq = 4000;
N = length(frame);
CC_count = triangle_count / 2;

% Compute periodogram
periodogram = fft(frame);
periodogram(:) = (abs(periodogram(:)).^2)/N;

% build filterBank
x = 1:triangle_count+2;
step = (melScale(end_freq) - melScale(start_freq))/(triangle_count+1);
freqs = ImelScale(melScale(start_freq)+step*(x-1));

filterbank = zeros(triangle_count, N);
step = (end_freq*2)/(N-1); %*2 per il teorema del campionamento
for v=1:triangle_count
    for n=1:N
        % triangle che parte da freqs(v) e termina a freqs(v+2)
        filterbank(v,n) = triangle((n-1)*step,freqs(v),freqs(v+2));
    end
end


% Extract mel energies of the first 13 triangles
CC = zeros( 1, CC_count );
for n=1:CC_count
    CC(n) = log(dot(filterbank(n,:),periodogram));
end
CC = dct(CC);



% Stampa roba
figure(1);
set(gcf,'numbertitle','off','name','Frequenze del filterBank');
x = 1:triangle_count+2;
plot(x,freqs,'*');
x = 1:N;
figure(2);
set(gcf,'numbertitle','off','name','Blocco del filterBank');
plot(x,filterbank(:,x));
end