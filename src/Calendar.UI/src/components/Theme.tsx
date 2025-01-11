import { Box, Switch } from "@edgmont-ui/react"
import { MoonIcon, SunIcon } from "@heroicons/react/16/solid"

type ThemeProps = {
  onChange: () => void;
  checked: boolean;
}

export const Theme: React.FC<ThemeProps> = ({ onChange, checked }) => {
  return (
    <Box display="flex" alignSelf="center" width="120px" justifyContent="space-between" borderRadius="8" border="border.1" px="3" py="3" bg="background">
      <SunIcon width="16px" />
      <Switch checked={checked} onChange={onChange} />
      <MoonIcon width="16px" />
    </Box>
  )
}