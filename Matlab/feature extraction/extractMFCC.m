function MFCC = extractMFCC( audio_sequence )

samples_in_frame = 256;
overlapping_factor = 3/4;
samples_count = length(audio_sequence);

start_index = 1;

% divide audio_sequence in frames
frame = zeros(1,samples_in_frame);
frames = frame;
while start_index < samples_count
    for n=1:samples_in_frame
        if(start_index + n < samples_count)
            frame(n) = audio_sequence(start_index+n);
        else
            frame(n) = 0; % zero filling
        end
    end
    start_index = start_index+(1-overlapping_factor)*samples_in_frame;
    frames = [frames;frame]; % appendi il frame a frames
end
frames = frames(2:end,:); % remove first row
d = size(frames);
frame_count = d(1);

CC = extractCC( frames(1,:) );
for f=2:frame_count
    CC = [CC;extractCC( frames(f,:) )]; %% append feature row
end
DD = extractDD(CC, 2); % M = 2 (precision)
MFCC = [CC DD];
end

