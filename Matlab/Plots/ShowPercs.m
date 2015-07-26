% shows percs LALALA
function [] = ShowPercs(Andrea, Davide, Emanuele, Model, ActualSpeaker, adjust)


%Create a custom colormap
%33 / x = 100 / 64 => x = 64/100*33
if adjust
    max = ceil(64/100*(80-33));
    min = 1;
else
    max = ceil(64/100*80);
    min = ceil(64/100*33);
end
cmap = zeros(64,3);
cmap(1:min,1) = 1;
for n=min+1:max
    lerpFactor = 1/(max-min-1)*(n-min-1);
    cmap(n,1) = 1 - lerpFactor;
    cmap(n,2) = lerpFactor;
end

for n=max:64
    cmap(n,2) = 1;
end

I = 1;
fig = 1;
maxPercs = 0;
MPercs = zeros(100,100);
while( 1 )
    F = I;
    if( F+1 <= length(Model) )
        while( strcmp(Model{F+1},Model{I}) == 1 ) 
            F = F+1;
            if( F+1 > length(Model) )
                break;
            end
        end
    end
    
    percs = zeros(F-I+1);
    xlabels = [];
    for n=I:F
        if(strcmp(ActualSpeaker{n},'ActualSpeaker = Davide') == 1)
            percs(n-I+1) = Davide(n) / (Andrea(n)+Davide(n)+Emanuele(n));
            MPercs(fig,n-I+1) = percs(n-I+1);
            xlabels = [xlabels; 'D'];
        else if(strcmp(ActualSpeaker{n},'ActualSpeaker = Emanuele') == 1)
                percs(n-I+1) = Emanuele(n) / (Andrea(n)+Davide(n)+Emanuele(n));
                MPercs(fig,n-I+1) = percs(n-I+1);
                xlabels = [xlabels; 'E'];
            else if(strcmp(ActualSpeaker{n},'ActualSpeaker = Andrea') == 1)
                    percs(n-I+1) = Andrea(n) / (Andrea(n)+Davide(n)+Emanuele(n));
                    MPercs(fig,n-I+1) = percs(n-I+1);
                    xlabels = [xlabels; 'A'];
                end
            end
        end
    end
    if(length(percs) > maxPercs) 
        maxPercs = length(percs);
    end
    
    
    %figure(fig);
    fig = fig+1;
    
    %b = bar(percs,0.4);
    %colormap(cmap);
    %colorbar;
    %set(gca, 'XTickLabel',xlabels);
    %set(gca, 'CLim', [0, 1]);

    
    %title(Model{I});
    
    I = F+1;
    if( I > length(Model) )
        fig = fig - 1;
        break;
    end
end
Laa = MPercs(1:fig,1:maxPercs);
if adjust
    for x=1:size(Laa,1)
        for y=1:size(Laa,2)
            Laa(x,y) = Laa(x,y)-0.3333;
            if(Laa(x,y) < 0)
                Laa(x,y) = 0;
        end
    end
    
end
figure(fig+1);
colormap(cmap);
colorbar;

h = bar3(Laa,1);
%camproj('perspective');
shading interp;
for i = 1:length(h)
    zdata = get(h(i),'ZData');
    set(h(i), 'CData', zdata); 
end
set(h, 'EdgeColor', 'black');
set(gca, 'CLim', [0, 1]);
%zlabel('Prediction correctness perc. (0-1)');
%xlabel('Model');
%ylabel('Audio');
if(adjust)
    zmax = 0.7;
else
    zmax = 1;
end

axis([0 (size(Laa,2)+1) 0 size(Laa,1)+1 0 zmax])


media = zeros(16,1);
for n=1:16
   md = 0;
   for i=1:5
      md = md + Laa(n,i);
   end
   md = md / 5;
   media(n) = md;
end
media = transpose(media);

a = 32; % questo serve solo per il breakpoint
end