function X=TransformDCT(Data,Type)
% this function is provide the programmer DCT-II and DCT-IV
% these two functions are used widely in Signal and image processing
% input :- 
%   Data = array of data
%   Type = 2 or 4
% output:-
%   X = stream of transformed data by DCT-II or DCT-IV
%Example :-X=[12 30 40 5];
% C=TransformDCT(X,2);
% by Mohammed Mustafa Siddeq
%     mamadmmx76@yahoo.com
%     Date - 16/11/2011
%--------------------------------------------------------------------
S_=size(Data);
N=S_(2);
%---------------DCT - II ------------------------------------------------
if (Type==2)
  Sk(1:N)=1; Sk(1)=1/sqrt(2);
   for k=0:N-1
     X(k+1)=0;
    for i=0:N-1
        X(k+1)=X(k+1)+(Data(i+1).*cos(pi.*k.*(i+0.5)./N));
    end;
     X(k+1)=Sk(k+1).*sqrt(2/N).*X(k+1);
   end;
end;
%------------------------'DCT -IV'....................;
if (Type==4)
  for k=0:N-1
     X(k+1)=0;
      for i=0:N-1
         X(k+1)=X(k+1)+(Data(i+1).*cos(pi.*(k+0.5).*(i+0.5)./N));
      end;
     X(k+1)=X(k+1).*sqrt(2/N);
  end;
end;
%%%--------------------------------------------------------------
end
