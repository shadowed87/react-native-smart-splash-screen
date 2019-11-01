
#import "RCTSplashScreen.h"

static RCTRootView *rootView = nil;

@interface RCTSplashScreen()

@end

@implementation RCTSplashScreen

RCT_EXPORT_MODULE(SplashScreen)

+ (BOOL) requiresMainQueueSetup
{
    return YES;
}

+ (void)open:(RCTRootView *)v {
    [RCTSplashScreen open:v withImageNamed:@"splash"];
}


+ (void)open:(RCTRootView *)v withImageNamed:(NSString *)imageName {
    rootView = v;

    NSString *imageUrl = [[NSUserDefaults standardUserDefaults] valueForKey:@"launchScreenImageUrl"];
    NSData *imageData = [[NSUserDefaults standardUserDefaults] valueForKey:@"launchScreenImageData"];
    UIImageView *view = [[UIImageView alloc]initWithFrame:[UIScreen mainScreen].bounds];
    // 判断本地是否保存有网络启动图，有则加载网络启动图，无则加载默认的启动页
    if (imageUrl.length > 0 && imageData.length > 0) {
        view.image = [UIImage imageWithData:imageData];
    } else {
        view.image = [UIImage imageNamed:imageName];
    }
    view.contentMode = UIViewContentModeScaleAspectFill;

    [[NSNotificationCenter defaultCenter] removeObserver:rootView  name:RCTContentDidAppearNotification object:rootView];
    
    [rootView setLoadingView:view];
}

RCT_EXPORT_METHOD(loadLaunchScreenImage:(NSString *)start_url iconUrl:(NSString *)icon_url ) {
    // 下载启动图，目前只用到start_url
    NSString *imageUrl = [[NSUserDefaults standardUserDefaults] valueForKey:@"launchScreenImageUrl"];
    if (![start_url isEqualToString:imageUrl]) {
        // 如果本地没有网络启动图，你下载网络启动图
        NSData *imageData = [NSData dataWithContentsOfURL:[NSURL URLWithString:start_url]];
        
        // 存储图片路径和图片，以便下次比较是否需要下载网络图片
        [[NSUserDefaults standardUserDefaults] setValue:start_url forKey:@"launchScreenImageUrl"];
        [[NSUserDefaults standardUserDefaults] setValue:imageData forKey:@"launchScreenImageData"];
        
    }
}
RCT_EXPORT_METHOD(cleanScreenImage) {
    [[NSUserDefaults standardUserDefaults] setValue:@"" forKey:@"launchScreenImageUrl"];
    [[NSUserDefaults standardUserDefaults] setValue:@"" forKey:@"launchScreenImageData"];
}

RCT_EXPORT_METHOD(close:(NSDictionary *)options) {
    if (!rootView) {
        return;
    }
    
    int animationType = UIAnimationNone;
    int duration = 0;
    int delay = 0;
    
    if(options != nil) {
        
        NSArray *keys = [options allKeys];
        
        if([keys containsObject:@"animationType"]) {
            animationType = [[options objectForKey:@"animationType"] intValue];
        }
        if([keys containsObject:@"duration"]) {
            duration = [[options objectForKey:@"duration"] intValue];
        }
        if([keys containsObject:@"delay"]) {
            delay = [[options objectForKey:@"delay"] intValue];
        }
    }

    if(animationType == UIAnimationNone) {
        rootView.loadingViewFadeDelay = 0;
        rootView.loadingViewFadeDuration = 0;
    }
    else {
        rootView.loadingViewFadeDelay = delay / 1000.0;
        rootView.loadingViewFadeDuration = duration / 1000.0;
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(rootView.loadingViewFadeDelay * NSEC_PER_SEC)),
                   dispatch_get_main_queue(),
                   ^{
                       [UIView animateWithDuration:rootView.loadingViewFadeDuration
                                        animations:^{
                                            if(animationType == UIAnimationScale) {
                                                rootView.loadingView.transform = CGAffineTransformMakeScale(1.5, 1.5);
                                                rootView.loadingView.alpha = 0;
                                            }
                                            else {
                                                rootView.loadingView.alpha = 0;
                                            }
                                        } completion:^(__unused BOOL finished) {
                                            [rootView.loadingView removeFromSuperview];
                                        }];
                   });
    
}

- (NSDictionary *)constantsToExport
{
    return @{
             @"animationType": @{
                     @"none": @(UIAnimationNone),
                     @"fade": @(UIAnimationFade),
                     @"scale": @(UIAnimationScale),
                 }
             };
}

@end
