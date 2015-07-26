function [] = RemoveSilence( data, fs, bitsPerSample, outputFname)

i = 1;
while(data(i) == 0)
    i = i+1;
end

newData = data(i:length(data));

audiowrite(outputFname,newData,fs,'BitsPerSample',bitsPerSample);
end

